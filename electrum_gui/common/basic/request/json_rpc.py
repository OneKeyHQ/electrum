from typing import Any, Callable, List, Tuple, Union

from requests import Response, Session

from electrum_gui.common.basic.request.exceptions import JsonRPCException, RequestException
from electrum_gui.common.basic.request.interfaces import JsonRPCInterface
from electrum_gui.common.basic.request.restful import RestfulRequest


class JsonRPCRequest(JsonRPCInterface):
    def __init__(
        self,
        url: str,
        timeout: int = 30,  # in seconds
        debug_mode: bool = False,
        session_initializer: Callable[[Session], None] = None,
    ):
        self.inner = RestfulRequest(
            base_url=url,
            timeout=timeout,
            response_jsonlize=True,
            debug_mode=debug_mode,
            session_initializer=session_initializer,
        )

    def call(
        self,
        method: str,
        params: Union[list, dict] = None,
        headers: dict = None,
        timeout: int = None,
        path: str = "",
        **kwargs,
    ) -> Union[Response, Any]:
        payload = self.normalize_params(method, params)
        try:
            resp = self.inner.post(path, json=payload, timeout=timeout, headers=headers, **kwargs)
        except RequestException:
            raise JsonRPCException("Json RPC call failed.")
        return self.parse_response(resp)

    def batch_call(
        self,
        calls: List[Tuple[str, Union[list, dict]]],
        headers: dict = None,
        timeout: int = None,
        path: str = "",
        **kwargs,
    ) -> Union[Response, List[Any]]:
        payload = [
            self.normalize_params(method, params, order_id=order_id) for order_id, (method, params) in enumerate(calls)
        ]
        try:
            resp = self.inner.post(path, json=payload, timeout=timeout, headers=headers, **kwargs)
        except RequestException:
            raise JsonRPCException("Json RPC call failed.")

        if not isinstance(resp, list):
            raise JsonRPCException(f"Responses of batch call should be a list, but got <{resp}>", json_response=resp)
        elif len(resp) != len(calls):
            raise JsonRPCException(f"Batch with {len(resp)} calls, but got {len(calls)} responses", json_response=resp)
        else:
            resp = sorted(resp, key=lambda i: int(i.get("id", 0)))
            results = [self.parse_response(i) for i in resp]
            return results

    @staticmethod
    def parse_response(response: dict, order_id: int = None) -> Any:
        resp_tag = "RPC response" if order_id is None else f"{order_id} response of batch"

        if not isinstance(response, dict):
            raise JsonRPCException(f"The {resp_tag} should be a dict, but got <{response}>", json_response=response)
        elif "error" in response:
            raise JsonRPCException(f"Error at the {resp_tag}. error: {response.get('error')}", json_response=response)
        elif "result" not in response:
            raise JsonRPCException(f"No 'result' found from the {resp_tag}.", json_response=response)
        else:
            return response["result"]

    @staticmethod
    def normalize_params(method: str, params: Union[list, dict], order_id: int = 0) -> dict:
        payload = dict(jsonrpc="2.0", method=method, id=order_id)

        if params is not None:
            payload["params"] = params

        return payload
