from .custom_objc import *

class CallHandler(OKNSObject):

    value = objc_property()

    @objc_method
    def init(self):
        return self
