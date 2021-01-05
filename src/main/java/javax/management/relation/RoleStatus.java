
    public static boolean isRoleStatus(int status) {
        if (status != NO_ROLE_WITH_NAME &&
            status != ROLE_NOT_READABLE &&
            status != ROLE_NOT_WRITABLE &&
            status != LESS_THAN_MIN_ROLE_DEGREE &&
            status != MORE_THAN_MAX_ROLE_DEGREE &&
            status != REF_MBEAN_OF_INCORRECT_CLASS &&
            status != REF_MBEAN_NOT_REGISTERED) {
            return false;
        }
        return true;
    }
}
