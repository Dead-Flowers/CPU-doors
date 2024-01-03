from enum import StrEnum, auto


class ErrorCode(StrEnum):
    VALIDATION_ERROR = auto()
    CHALLENGE_FAILED_ERROR = auto()