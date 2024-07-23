package com.woory.backend.error;

public enum ErrorCode {
	// Common

	INTERNAL_SERVER_ERROR(500, "COMMON_001", "예기치 못한 에러가 발생하였습니다."),
	FILE_SIZE_EXCEED(400, "COMMON_002", "파일 크기는 5MB를 초과할 수 없습니다."),
	FILE_IS_NOT_IMAGE(400, "COMMON_003", "파일 확장자는 png 또는 jpg만 가능합니다."),
	FILE_DOES_NOT_EXIST(400, "COMMON_004", "파일이 존재하지 않습니다."),
	INVALID_FILE_NAME(400, "COMMON_005", "파일 이름이 유효하지 않습니다."),
	ERROR_SAVING_FILE(500, "COMMON_006", "파일 저장중 문제가 발생했습니다."),
	ERROR_DELETING_FILE(500, "COMMON_007", "파일 삭제중 문제가 발생했습니다."),
	// CommentService
	USER_NOT_FOUND_IN_GROUP(404, "COMMENT_001", "가족에서 사용자를 찾을수 없습니다."),
	USER_BANNED_OR_NON_MEMBER(403, "COMMENT_002", "가족에 속한 사용자가 아닙니다."),
	COMMENT_NOT_FOUND(404, "COMMENT_003", "해당 댓글을 찾을 수 없습니다."),
	REPLY_TO_REPLY_NOT_ALLOWED(400, "COMMENT_004", "대댓글에 대댓글은 할 수 없습니다."),
	NOT_COMMENT_AUTHOR(403, "COMMENT_005", "해당 댓글을 삭제할 권한이 없습니다."),
  PARENT_COMMENT_NOT_FOUND(404, "COMMENT_006", "부모 댓글을 찾을 수 없습니다."),

	TOPIC_NOT_FOUND(404, "TOPIC_001", "토픽 세트를 찾을 수 없습니다."),
	// ContentService

	GROUP_NOT_FOUND(404, "CONTENT_001", "해당 사용자 또는 가족을 찾을 수 없습니다."),
	NO_PERMISSION_TO_DELETE(403, "CONTENT_002", "컨텐츠를 삭제할 권한이 없습니다."),
	NO_PERMISSION_TO_UPDATE(403, "CONTENT_003", "컨텐츠를 수정할 권한이 없습니다"),
	INVALID_DATE_FORMAT(400, "CONTENT_004", "날짜표기가 아닌 데이터입니다."),
	CONTENT_NOT_FOUND(404, "CONTENT_005", "해당 컨텐츠를 찾을 수 없습니다."),

	GROUP_CREATION_LIMIT_EXCEEDED(400, "GROUP_001", "사용자는 5개 이상의 가족그룹을 가질수 없습니다."),
	NO_PERMISSION_TO_DELETE_GROUP(403, "GROUP_002", "가족을 삭제할 권한을 가지고 있지 않습니다."),
	USER_ALREADY_MEMBER(400, "GROUP_003", "사용자는 이미 가족에 포함되어있습니다."),
	NO_PERMISSION_TO_UPDATE_GROUP(400, "GROUP_004", "가족을 수정할 권한이 없습니다."),
	// UserService,
	USER_GROUPS_NOT_FOUND(404, "USER_001", "유저,가족의 정보를 찾을 수 없습니다."),
	USER_NOT_FOUND(404, "USER_002", "해당 유저를 찾을수 없습니다.");

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