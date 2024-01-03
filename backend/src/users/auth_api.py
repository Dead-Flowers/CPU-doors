
from ninja_extra import api_controller, NinjaExtraAPI
from ninja_jwt.controller import TokenObtainPairController

@api_controller('/auth/token', tags=['Auth'])
class JwtAuthController(TokenObtainPairController):
    """obtain_token and refresh_token only"""
