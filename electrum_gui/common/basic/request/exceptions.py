from typing import Any

from requests import Response


class RequestException(IOError):
    pass


class ResponseException(IOError):
    def __init__(self, message: str, response: Response):
        self.message = message
        self.response = response

        super(ResponseException, self).__init__(message)


class JsonRPCException(IOError):
    def __init__(self, message: str, json_response: Any):
        self.message = message
        self.json_response = json_response

        super(JsonRPCException, self).__init__(message)


class RPCError(Exception):
    def __init__(self, code, message):
        super().__init__(f"rpc error: with code: {code} and message: {message}")
