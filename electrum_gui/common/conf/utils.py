import os

from electrum import rsakey

_PUBKEY = {
    "n": int(
        "0x00e2e9d609a6538d4964bff96588b3349fb3dd670474dde130b5358714c62f9933eaf95f2924045658406bfd38966dcb4568f327e4e6"
        "da965b4619e9aef7d93c577d1e7a13d8f551d5bed9bd7575e9de296719b3ee285f13de064bff95e8db5185560a4fc12427be26d4974f04"
        "4845699267d28236046a467c78b0d04ff4999aaf37190e8e13a6eee325b2deba1ed926d881e4f401cdb8782cec5ef340b0590cd5410df1"
        "a0859e97916bad9ad70b03904344e9619f495ec209b5f425a5d6bfaae2e676226122b269b9d9f8d83d268b73bc3dba6669408acfd98b2a"
        "1116352532bb8dc9e0697dbacf1d492ccc942fba2f2c03f7b144b0cf3036087df448a1ef6d3ae5fa006c431295afe802f74fc4ea6b0a17"
        "9c30506bfae17e3ffd4fd86165db766fe4052868736c2bfc54a0980d514fbf881a61432e434f5b47e8b190c14aa428c9d746759baf5ef9"
        "9a7a3a90a0e5007b9f568516bc85c92e92209de33df4b7e82e947777c2ac1109048016749259566d83e77436f4715c9febb728ceeb0c60"
        "2737cff954a5296df148c555d44cee2555ab8eb42277444580af33f828a9f6d32b599e10300b25e9ab424a809cc712b420a397a4b01f4a"
        "6195b6d03276a265ebad1a00b703780a59752e25b1a59358429037b221cf56ca76668dd3a59f3cf950ce61390a088d184515bf0989e05b"
        "60117bb55395db83fdb5866ff9964cc961c4cf",
        16,
    ),
    "e": 0x10001,
}
_RSAKEY = None


def verify(signature: bytes, data: bytes) -> bool:
    global _RSAKEY
    if _RSAKEY is None:
        _RSAKEY = rsakey.RSAKey(n=_PUBKEY["n"], e=_PUBKEY["e"])

    return _RSAKEY.hashAndVerify(signature, data)


def get_data_dir():
    environ = os.environ

    if "ELECTRUMDIR" in environ:
        return environ["ELECTRUMDIR"]
    elif "iOS_DATA" in environ:
        return environ["iOS_DATA"]
    elif 'ANDROID_DATA' in environ:
        from com.chaquo.python import Python  # noqa

        context = Python.getPlatform().getApplication()
        return context.getFilesDir().getPath() + '/data'
    elif os.name == 'posix':
        return os.path.join(environ["HOME"], ".electrum")
    elif "APPDATA" in environ:
        return os.path.join(environ["APPDATA"], "Electrum")
    elif "LOCALAPPDATA" in environ:
        return os.path.join(environ["LOCALAPPDATA"], "Electrum")
    else:
        return "."
