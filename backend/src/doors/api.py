from django.http import JsonResponse
from ninja_extra import NinjaExtraAPI
from django.core.exceptions import ValidationError
from doors.constants import ErrorCode
from doors.exceptions import ChallengeFailedError
from users.api import router as users_router
from users.auth_api import JwtAuthController
from controllers.api import router as controllers_api
api = NinjaExtraAPI()

@api.exception_handler(ValidationError)
def django_validation_error(request, exc: ValidationError):
    return JsonResponse(
        {
            "message": "Invalid request",
            "code": ErrorCode.VALIDATION_ERROR,
            "detail": exc.messages[0],
            "error_dict": exc.message_dict if hasattr(exc, 'error_dict') else dict(),
        },
        status=400,
    )

@api.exception_handler(ChallengeFailedError)
def challenge_failed_error(request, exc):
    return JsonResponse(
        {
            "message": str(exc),
            "code": ErrorCode.CHALLENGE_FAILED_ERROR,
        },
        status=403,
    )
api.add_router('users/', users_router)
api.add_router('controllers/', controllers_api)
api.register_controllers(JwtAuthController)