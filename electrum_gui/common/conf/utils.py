import os


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
