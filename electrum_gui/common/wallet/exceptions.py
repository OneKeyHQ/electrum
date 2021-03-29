class AdapterClassNotFound(Exception):
    def __init__(self, chain_code: str, expected_class_name: str):
        super(AdapterClassNotFound, self).__init__(
            f"chain_code: {repr(chain_code)}, expected_class_name: {repr(expected_class_name)}"
        )
