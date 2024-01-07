from django.http import JsonResponse
from ninja_extra import NinjaExtraAPI
from django.core.exceptions import ValidationError
from doors.constants import ErrorCode
from doors.exceptions import ApiException, ChallengeFailedError
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
            "error_dict": exc.message_dict if hasattr(exc, "error_dict") else dict(),
        },
        status=400,
    )


@api.exception_handler(ApiException)
def api_error(request, exc: ApiException):
    return JsonResponse(
        {
            "message": str(exc),
            "code": exc.code,
        },
        status=exc.status_code,
    )


api.add_router("users/", users_router)
api.add_router("controllers/", controllers_api)
api.register_controllers(JwtAuthController)
