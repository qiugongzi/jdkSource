
                    x = ((Integer) gutterOffsetObject).intValue() - c.getX();
                    x += WindowsPopupMenuUI.getGutterWidth();
                }
            }
            Skin skin = xp.getSkin(c, Part.MP_POPUPSEPARATOR);
            int skinHeight = skin.getHeight();
            int y = (s.height - skinHeight) / 2;
            skin.paintSkin(g, x, y, s.width - x - 1, skinHeight, State.NORMAL);
        } else {
            int y = s.height / 2;
            g.setColor(c.getForeground());
            g.drawLine(1, y - 1, s.width - 2, y - 1);

            g.setColor(c.getBackground());
            g.drawLine(1, y,     s.width - 2, y);
        }
    }

    public Dimension getPreferredSize(JComponent c) {
        int fontHeight = 0;
        Font font = c.getFont();
        if (font != null) {
            fontHeight = c.getFontMetrics(font).getHeight();
        }

        return new Dimension(0, fontHeight/2 + 2);
    }

}
