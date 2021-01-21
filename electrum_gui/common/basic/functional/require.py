from typing import Any


def require(statement: bool, message: str = None):
    if not statement:
        message = message or "raising by require"
        raise AssertionError(message)


def require_not_none(obj: Any, message: str = None) -> Any:
    if obj is None:
        message = message or "require not none but none found"
        raise AssertionError(message)

    return obj


def require_none(obj: Any, message: str = None):
    if obj is None:
        message = message or f"require none but {repr(obj)} found"
        raise AssertionError(message)
