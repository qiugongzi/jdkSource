
@jdk.Exported
public interface DocCommentTree extends DocTree {
    List<? extends DocTree> getFirstSentence();
    List<? extends DocTree> getBody();
    List<? extends DocTree> getBlockTags();
}
