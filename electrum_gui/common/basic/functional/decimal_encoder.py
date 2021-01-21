import json
from decimal import Decimal


class DecimalEncoder(json.JSONEncoder):
    def default(self, o):
        return (
            str(o) if isinstance(o, Decimal) else super(DecimalEncoder, self).default(o)
        )
