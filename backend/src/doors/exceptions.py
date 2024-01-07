from doors.constants import ErrorCode


class ApiException(Exception):
    def __init__(self, error_code: str, message: str, status_code: int) -> None:
        super().__init__(message)
        self.code = error_code
        self.status_code = status_code


class ChallengeFailedError(ApiException):
    def __init__(self, message: str) -> None:
        super().__init__(ErrorCode.CHALLENGE_FAILED_ERROR, message, 403)
