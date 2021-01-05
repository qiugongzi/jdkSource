

package com.sun.java.swing.plaf.windows;

import java.security.AccessController;
import sun.security.action.GetBooleanAction;

import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



import sun.swing.UIClientPropertyKey;
import com.sun.java.swing.plaf.windows.TMSchema.State;
import static com.sun.java.swing.plaf.windows.TMSchema.State.*;
import com.sun.java.swing.plaf.windows.TMSchema.Part;
import com.sun.java.swing.plaf.windows.TMSchema.Prop;
import com.sun.java.swing.plaf.windows.XPStyle.Skin;

import sun.awt.AppContext;


class AnimationController implements ActionListener, PropertyChangeListener {

    private final static boolean VISTA_ANIMATION_DISABLED =
        AccessController.doPrivileged(new GetBooleanAction("swing.disablevistaanimation"));


    private final static Object ANIMATION_CONTROLLER_KEY =
        new StringBuilder("ANIMATION_CONTROLLER_KEY");

    private final Map<JComponent, Map<Part, AnimationState>> animationStateMap =
            new WeakHashMap<JComponent, Map<Part, AnimationState>>();

    private final javax.swing.Timer timer =
        new javax.swing.Timer(1000/30, this);

    private static synchronized AnimationController getAnimationController() {
        AppContext appContext = AppContext.getAppContext();
        Object obj = appContext.get(ANIMATION_CONTROLLER_KEY);
        if (obj == null) {
            obj = new AnimationController();
            appContext.put(ANIMATION_CONTROLLER_KEY, obj);
        }
        return (AnimationController) obj;
    }

    private AnimationController() {
        timer.setRepeats(true);
        timer.setCoalesce(true);
        UIManager.addPropertyChangeListener(this);
    }

    private static void triggerAnimation(JComponent c,
                           Part part, State newState) {
        if (c instanceof javax.swing.JTabbedPane
            || part == Part.TP_BUTTON) {
            return;
        }
        AnimationController controller =
            AnimationController.getAnimationController();
        State oldState = controller.getState(c, part);
        if (oldState != newState) {
            controller.putState(c, part, newState);
            if (newState == State.DEFAULTED) {
                oldState = State.HOT;
            }
            if (oldState != null) {
                long duration;
                if (newState == State.DEFAULTED) {
                    duration = 1000;
                } else {
                    XPStyle xp = XPStyle.getXP();
                    duration = (xp != null)
                               ? xp.getThemeTransitionDuration(
                                       c, part,
                                       normalizeState(oldState),
                                       normalizeState(newState),
                                       Prop.TRANSITIONDURATIONS)
                               : 1000;
                }
                controller.startAnimation(c, part, oldState, newState, duration);
            }
        }
    }

    private static State normalizeState(State state) {
        State rv;
        switch (state) {
        case DOWNPRESSED:

        case LEFTPRESSED:

        case RIGHTPRESSED:
            rv = UPPRESSED;
            break;

        case DOWNDISABLED:

        case LEFTDISABLED:

        case RIGHTDISABLED:
            rv = UPDISABLED;
            break;

        case DOWNHOT:

        case LEFTHOT:

        case RIGHTHOT:
            rv = UPHOT;
            break;

        case DOWNNORMAL:

        case LEFTNORMAL:

        case RIGHTNORMAL:
            rv = UPNORMAL;
            break;

        default :
            rv = state;
            break;
        }
        return rv;
    }

    private synchronized State getState(JComponent component, Part part) {
        State rv = null;
        Object tmpObject =
            component.getClientProperty(PartUIClientPropertyKey.getKey(part));
        if (tmpObject instanceof State) {
            rv = (State) tmpObject;
        }
        return rv;
    }

    private synchronized void putState(JComponent component, Part part,
                                       State state) {
        component.putClientProperty(PartUIClientPropertyKey.getKey(part),
                                    state);
    }

    private synchronized void startAnimation(JComponent component,
                                     Part part,
                                     State startState,
                                     State endState,
                                     long millis) {
        boolean isForwardAndReverse = false;
        if (endState == State.DEFAULTED) {
            isForwardAndReverse = true;
        }
        Map<Part, AnimationState> map = animationStateMap.get(component);
        if (millis <= 0) {
            if (map != null) {
                map.remove(part);
                if (map.size() == 0) {
                    animationStateMap.remove(component);
                }
            }
            return;
        }
        if (map == null) {
            map = new EnumMap<Part, AnimationState>(Part.class);
            animationStateMap.put(component, map);
        }
        map.put(part,
                new AnimationState(startState, millis, isForwardAndReverse));
        if (! timer.isRunning()) {
            timer.start();
        }
    }

    static void paintSkin(JComponent component, Skin skin,
                      Graphics g, int dx, int dy, int dw, int dh, State state) {
        if (VISTA_ANIMATION_DISABLED) {
            skin.paintSkinRaw(g, dx, dy, dw, dh, state);
            return;
        }
        triggerAnimation(component, skin.part, state);
        AnimationController controller = getAnimationController();
        synchronized (controller) {
            AnimationState animationState = null;
            Map<Part, AnimationState> map =
                controller.animationStateMap.get(component);
            if (map != null) {
                animationState = map.get(skin.part);
            }
            if (animationState != null) {
                animationState.paintSkin(skin, g, dx, dy, dw, dh, state);
            } else {
                skin.paintSkinRaw(g, dx, dy, dw, dh, state);
            }
        }
    }

    public synchronized void propertyChange(PropertyChangeEvent e) {
        if ("lookAndFeel" == e.getPropertyName()
            && ! (e.getNewValue() instanceof WindowsLookAndFeel) ) {
            dispose();
        }
    }

    public synchronized void actionPerformed(ActionEvent e) {
        java.util.List<JComponent> componentsToRemove = null;
        java.util.List<Part> partsToRemove = null;
        for (JComponent component : animationStateMap.keySet()) {
            component.repaint();
            if (partsToRemove != null) {
                partsToRemove.clear();
            }
            Map<Part, AnimationState> map = animationStateMap.get(component);
            if (! component.isShowing()
                  || map == null
                  || map.size() == 0) {
                if (componentsToRemove == null) {
                    componentsToRemove = new ArrayList<JComponent>();
                }
                componentsToRemove.add(component);
                continue;
            }
            for (Part part : map.keySet()) {
                if (map.get(part).isDone()) {
                    if (partsToRemove == null) {
                        partsToRemove = new ArrayList<Part>();
                    }
                    partsToRemove.add(part);
                }
            }
            if (partsToRemove != null) {
                if (partsToRemove.size() == map.size()) {
                    if (componentsToRemove == null) {
                        componentsToRemove = new ArrayList<JComponent>();
                    }
                    componentsToRemove.add(component);
                } else {
                    for (Part part : partsToRemove) {
                        map.remove(part);
                    }
                }
            }
        }
        if (componentsToRemove != null) {
            for (JComponent component : componentsToRemove) {
                animationStateMap.remove(component);
            }
        }
        if (animationStateMap.size() == 0) {
            timer.stop();
        }
    }

    private synchronized void dispose() {
        timer.stop();
        UIManager.removePropertyChangeListener(this);
        synchronized (AnimationController.class) {
            AppContext.getAppContext()
                .put(ANIMATION_CONTROLLER_KEY, null);
        }
    }

    private static class AnimationState {
        private final State startState;

        private final long duration;

        private long startTime;

        private boolean isForward = true;

        private boolean isForwardAndReverse;

        private float progress;

        AnimationState(final State startState,
                       final long milliseconds,
                       boolean isForwardAndReverse) {
            assert startState != null && milliseconds > 0;
            assert SwingUtilities.isEventDispatchThread();

            this.startState = startState;
            this.duration = milliseconds * 1000000;
            this.startTime = System.nanoTime();
            this.isForwardAndReverse = isForwardAndReverse;
            progress = 0f;
        }
        private void updateProgress() {
            assert SwingUtilities.isEventDispatchThread();

            if (isDone()) {
                return;
            }
            long currentTime = System.nanoTime();

            progress = ((float) (currentTime - startTime))
                / duration;
            progress = Math.max(progress, 0); if (progress >= 1) {
                progress = 1;
                if (isForwardAndReverse) {
                    startTime = currentTime;
                    progress = 0;
                    isForward = ! isForward;
                }
            }
        }
        void paintSkin(Skin skin, Graphics _g,
                       int dx, int dy, int dw, int dh, State state) {
            assert SwingUtilities.isEventDispatchThread();

            updateProgress();
            if (! isDone()) {
                Graphics2D g = (Graphics2D) _g.create();
                skin.paintSkinRaw(g, dx, dy, dw, dh, startState);
                float alpha;
                if (isForward) {
                    alpha = progress;
                } else {
                    alpha = 1 - progress;
                }
                g.setComposite(AlphaComposite.SrcOver.derive(alpha));
                skin.paintSkinRaw(g, dx, dy, dw, dh, state);
                g.dispose();
            } else {
                skin.paintSkinRaw(_g, dx, dy, dw, dh, state);
            }
        }
        boolean isDone() {
            assert SwingUtilities.isEventDispatchThread();

            return  progress >= 1;
        }
    }

    private static class PartUIClientPropertyKey
          implements UIClientPropertyKey {

        private static final Map<Part, PartUIClientPropertyKey> map =
            new EnumMap<Part, PartUIClientPropertyKey>(Part.class);

        static synchronized PartUIClientPropertyKey getKey(Part part) {
            PartUIClientPropertyKey rv = map.get(part);
            if (rv == null) {
                rv = new PartUIClientPropertyKey(part);
                map.put(part, rv);
            }
            return rv;
        }

        private final Part part;
        private PartUIClientPropertyKey(Part part) {
            this.part  = part;
        }
        public String toString() {
            return part.toString();
        }
    }
}
