
public interface RepositoryIdUtility
{
    boolean isChunkedEncoding(int valueTag);
    boolean isCodeBasePresent(int valueTag);




    int NO_TYPE_INFO = RepositoryId.kNoTypeInfo;
    int SINGLE_REP_TYPE_INFO = RepositoryId.kSingleRepTypeInfo;
    int PARTIAL_LIST_TYPE_INFO = RepositoryId.kPartialListTypeInfo;



    int getTypeInfo(int valueTag);


    int getStandardRMIChunkedNoRepStrId();
    int getCodeBaseRMIChunkedNoRepStrId();
    int getStandardRMIChunkedId();
    int getCodeBaseRMIChunkedId();
    int getStandardRMIUnchunkedId();
    int getCodeBaseRMIUnchunkedId();
    int getStandardRMIUnchunkedNoRepStrId();
    int getCodeBaseRMIUnchunkedNoRepStrId();
}
