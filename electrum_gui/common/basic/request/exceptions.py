from requests import Response


class ResponseException(IOError):
    def __init__(self, message: str, response: Response):
        self.message = message
        self.response = response

        super(ResponseException, self).__init__(message)
