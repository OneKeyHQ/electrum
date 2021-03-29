from dataclasses import dataclass
from typing import Optional

from electrum_gui.common.basic.dataclass.dataclass import DataClassMixin


@dataclass
class AddressValidation(DataClassMixin):
    is_valid: bool
    format: Optional[str] = None
