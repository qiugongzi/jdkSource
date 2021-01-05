
    void handleInput(Message header) throws IOException;


    void handleInput(RequestMessage_1_0 header) throws IOException;
    void handleInput(RequestMessage_1_1 header) throws IOException;
    void handleInput(RequestMessage_1_2 header) throws IOException;


    void handleInput(ReplyMessage_1_0 header) throws IOException;
    void handleInput(ReplyMessage_1_1 header) throws IOException;
    void handleInput(ReplyMessage_1_2 header) throws IOException;


    void handleInput(LocateRequestMessage_1_0 header) throws IOException;
    void handleInput(LocateRequestMessage_1_1 header) throws IOException;
    void handleInput(LocateRequestMessage_1_2 header) throws IOException;


    void handleInput(LocateReplyMessage_1_0 header) throws IOException;
    void handleInput(LocateReplyMessage_1_1 header) throws IOException;
    void handleInput(LocateReplyMessage_1_2 header) throws IOException;


    void handleInput(FragmentMessage_1_1 header) throws IOException;
    void handleInput(FragmentMessage_1_2 header) throws IOException;


    void handleInput(CancelRequestMessage header) throws IOException;
}
