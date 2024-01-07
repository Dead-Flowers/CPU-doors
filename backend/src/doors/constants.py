from enum import StrEnum, auto


class ErrorCode(StrEnum):
    VALIDATION_ERROR = auto()
    CHALLENGE_FAILED_ERROR = auto()
    INVALID_STATE = auto()
    INVALID_OPERATION = auto()
    TIMEOUT = auto()
