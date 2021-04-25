from typing import Any, Callable, Union
from urllib.parse import urljoin

from requests import RequestException, Response, Session

from electrum_gui.common.basic.request import exceptions
from electrum_gui.common.basic.request.enums import Method
from electrum_gui.common.basic.request.interfaces import RestfulInterface


class RestfulRequest(RestfulInterface):
    __DEFAULT_HEADER__ = {"User-Agent": "Electrum"}

    def __init__(
        self,
        base_url: str,
        timeout: int = 30,  # in seconds
        response_jsonlize: bool = True,
        debug_mode: bool = False,
        session_initializer: Callable[[Session], None] = None,
    ):
        self.base_url = base_url
        self.timeout = timeout
        self.response_jsonlize = response_jsonlize
        self.debug_mode = debug_mode
        self.session = self._new_session()

        if session_initializer:
            session_initializer(self.session)

    @classmethod
    def _new_session(cls):
        session = Session()
        session.headers.update(cls.__DEFAULT_HEADER__)
        return session

    def __str__(self):
        return (
            f"base_url: {self.base_url}, "
            f"timeout: {self.timeout}, "
            f"response_jsonlize: {self.response_jsonlize}, "
            f"debug_mode: {self.debug_mode}"
        )

    def request(
        self,
        method: Method,
        path: str,
        params: Any = None,
        data: Any = None,
        json: Any = None,
        headers: dict = None,
        timeout: int = None,
        **kwargs,
    ) -> Union[dict, Response]:
        url = urljoin(self.base_url, path)
        method = method.as_str()
        timeout = timeout or self.timeout
        args_str = (
            f"url: {url}, "
            f"method: {method}, "
            f"params: {params}, "
            f"data: {data}, "
            f"json: {json}, "
            f"headers: {headers}, "
            f"timeout: {timeout}, "
            f"others: {kwargs}, "
            f"restful_instance: <{self}>"
        )

        try:
            self.print_if_debug(f"Start requesting. {args_str}")

            response = self.session.request(
                method=method,
                url=url,
                params=params,
                data=data,
                json=json,
                headers=headers,
                timeout=timeout,
                **kwargs,
            )
        except RequestException as e:
            self.print_if_debug(f"Error in sending a request. {args_str}, exception: {e}")
            raise exceptions.RequestException()

        if not response.ok:
            message = (
                f"Something wrong in response. {args_str}, "
                f"status_code: {response.status_code}, "
                f"response_text: {response.text}"
            )
            self.print_if_debug(message)
            raise exceptions.ResponseException(message, response=response)

        if not self.response_jsonlize:
            return response

        try:
            return response.json()
        except ValueError as e:
            message = f"Error in parse response to json. {args_str}, " f"response_text: {response.text}, exception: {e}"
            self.print_if_debug(message)
            raise exceptions.ResponseException(message, response=response)

    def print_if_debug(self, message: str):
        if self.debug_mode and message:
            print(message)
