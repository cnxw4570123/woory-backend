package com.woory.backend.error;

public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(400, "COMMON_001", "Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "COMMON_002", "Method not allowed"),
    HANDLE_ACCESS_DENIED(403, "COMMON_003", "Access is Denied"),

    // Standard
    ILLEGAL_STATE(400, "STANDARD_001", "Illegal state"),
    ILLEGAL_ARGUMENT(400, "STANDARD_002", "Illegal argument"),

    // CommentService
    USER_NOT_FOUND_IN_GROUP(404, "COMMENT_001", "User not found in group"),
    USER_BANNED_OR_NON_MEMBER(403, "COMMENT_002", "User is banned or not a member of the group"),
    CONTENT_NOT_FOUND(404, "COMMENT_003", "Content not found"),
    USER_NOT_FOUND(404, "COMMENT_004", "User not found"),
    COMMENT_NOT_FOUND(404, "COMMENT_005", "Comment not found"),
    REPLY_TO_REPLY_NOT_ALLOWED(400, "COMMENT_006", "Cannot reply to a reply"),
    NOT_COMMENT_AUTHOR(403, "COMMENT_007", "Only the comment author can delete this comment"),
    INTERNAL_SERVER_ERROR(500, "COMMENT_008", "An unexpected error occurred");

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public int getStatus() { return status; }
}
