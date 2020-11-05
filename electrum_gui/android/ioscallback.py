from .custom_objc import *

class CallHandler(OKNSObject):

    value = objc_property()

    @objc_method
    def initWithValue(self, v: int):
        self.value = v
        return self
