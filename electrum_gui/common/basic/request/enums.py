from enum import IntEnum, unique


@unique
class Method(IntEnum):
    GET = 1
    POST = 2

    def as_str(self) -> str:
        a = ""
        return "GET" if self == Method.GET else "POST"
