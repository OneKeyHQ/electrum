from abc import ABC, abstractmethod
from typing import Union, Any

from electrum_gui.common.basic.request.enums import Method
from requests import Response


class RestfulInterface(ABC):
    def get(
        self,
        path: str,
        params: Any = None,
        headers: dict = None,
        timeout: int = None,
        **kwargs
    ) -> Union[dict, Response]:
        """
        GET a request

        :param path: target path
        :param params: request parameter, optional
        :param headers: request header, optional
        :param timeout: request timeout, optional
        :return: json dict or Response object
        """
        return self.request(
            method=Method.GET,
            path=path,
            params=params,
            headers=headers,
            timeout=timeout,
            **kwargs
        )

    def post(
        self,
        path: str,
        data: Any = None,
        json: Any = None,
        headers: dict = None,
        timeout: int = None,
        **kwargs
    ) -> Union[dict, Response]:
        """
        POST a request

        :param path: target path
        :param data: request data, optional
        :param json: request json, replace data field if specified, optional
        :param headers: request header, optional
        :param timeout: request timeout, optional
        :return: json dict or Response object
        """
        return self.request(
            method=Method.POST,
            path=path,
            data=data,
            json=json,
            headers=headers,
            timeout=timeout,
            **kwargs
        )

    @abstractmethod
    def request(
        self,
        method: Method,
        path: str,
        params: Any = None,
        data: Any = None,
        json: Any = None,
        headers: dict = None,
        timeout: int = None,
        **kwargs
    ) -> Union[dict, Response]:
        """
        Send a request

        :param method: enum, GET or POST
        :param path: target path
        :param params: request parameter, optional
        :param data: request data, POST method only, optional
        :param json: request json, POST method only, optional
        :param headers: request header, optional
        :param timeout: request timeout, optional
        :return: json dict or Response object
        """
