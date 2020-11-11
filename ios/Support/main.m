//
    // main.m
    //
    #import <Foundation/Foundation.h>
    #import <UIKit/UIKit.h>
    #include <Python.h>
    #include <dlfcn.h>
    #import <AppDelegate.h>


#define load(HANDLE) handle = dlopen(file.path.UTF8String, RTLD_GLOBAL); HANDLE = handle;
// 😁 loading cryptodomex dynamic dependencies _cffi_backend.cpython-38-darwin.so , created by sweepmonkli
void  loading_cffi_backend() {
    void *_cffi_backend = NULL;
    NSString *fileStr = [NSString stringWithFormat:@"%@/Library/Application Support/com.c3-soft.OneKey/app/OneKey/CFFI/_cffi_backend.framework/_cffi_backend.cpython-38-darwin.so", [[NSBundle mainBundle] resourcePath]];
    if ([NSURL fileURLWithPath:(fileStr) isDirectory: NO]) {
        NSURL *file = [NSURL fileURLWithPath:fileStr];
        void *handle;
        load(_cffi_backend);
        PyObject* (*func)(void);
        func = dlsym(_cffi_backend, "PyInit__cffi_backend");
        PyImport_AppendInittab("_cffi_backend", func);
    } else {
        NSLog(@"_cffi_backend dylib is not exist");
        exit(1);
    }
}

void bitarray_importer() {
    PyRun_SimpleString(
        "import sys, importlib\n" \
        "class BitArrayImporter(object):\n" \
        "    def find_module(self, fullname, mpath=None):\n" \
        "        if fullname in (" \
        "                    'bitarray._bitarray', " \
        "                    'bitarray._util', " \
        "                ):\n" \
        "            return self\n" \
        "        return\n" \
        "    def load_module(self, fullname):\n" \
        "        f = '__' + fullname.replace('.', '_')\n" \
        "        mod = sys.modules.get(f)\n" \
        "        if mod is None:\n" \
        "            mod = importlib.__import__(f)\n" \
        "            sys.modules[fullname] = mod\n" \
        "            return mod\n" \
        "        return mod\n" \
        "sys.meta_path.append(BitArrayImporter())");
}

void cytoolz_importer() {
    PyRun_SimpleString(
        "import sys, importlib\n" \
        "class CytoolzImporter(object):\n" \
        "    def find_module(self, fullname, mpath=None):\n" \
        "        if fullname in (" \
        "                    'cytoolz.dicttoolz', " \
        "                    'cytoolz.functoolz', " \
        "                    'cytoolz.itertoolz', " \
        "                    'cytoolz.recipes', " \
        "                    'cytoolz.utils', " \
        "                ):\n" \
        "            return self\n" \
        "        return\n" \
        "    def load_module(self, fullname):\n" \
        "        f = '__' + fullname.replace('.', '_')\n" \
        "        mod = sys.modules.get(f)\n" \
        "        if mod is None:\n" \
        "            mod = importlib.__import__(f)\n" \
        "            sys.modules[fullname] = mod\n" \
        "            return mod\n" \
        "        return mod\n" \
        "sys.meta_path.append(CytoolzImporter())");
}

void loading_lru() {
    void *lru = NULL;
    NSString *fileStr = [NSString stringWithFormat:@"%@/Library/Application Support/com.c3-soft.OneKey/app/OneKey/LRU/lru.cpython-38-darwin.so", [[NSBundle mainBundle] resourcePath]];
    if ([NSURL fileURLWithPath:(fileStr) isDirectory: NO]) {
        NSURL *file = [NSURL fileURLWithPath:fileStr];
        void *handle;
        load(lru);
        NSLog(@"loading...............lru");
        PyObject* (*func)(void);
        func = dlsym(lru, "PyInit_lru");
        PyImport_AppendInittab("lru", func);
    } else {
        NSLog(@"lru dylib is not exist");
        exit(1);
    }
}

void loading_bitarray_bitarray() {
    void *_bitarray = NULL;
    NSString *fileStr = [NSString stringWithFormat:@"%@/Library/Application Support/com.c3-soft.OneKey/app/OneKey/bitarray/_bitarray.cpython-38-darwin.so", [[NSBundle mainBundle] resourcePath]];
    if ([NSURL fileURLWithPath:(fileStr) isDirectory: NO]) {
        NSURL *file = [NSURL fileURLWithPath:fileStr];
        void *handle;
        load(_bitarray);
        NSLog(@"loading...............bitarry._bitarray");
        PyObject* (*func)(void);
        func = dlsym(_bitarray, "PyInit__bitarray");
        PyImport_AppendInittab("__bitarray__bitarray", func);
    } else {
        NSLog(@"_bitarray dylib is not exist");
        exit(1);
    }
}

void  loading_bitarray_util() {
    void *_util = NULL;
    NSString *fileStr = [NSString stringWithFormat:@"%@/Library/Application Support/com.c3-soft.OneKey/app/OneKey/bitarray/_util.cpython-38-darwin.so", [[NSBundle mainBundle] resourcePath]];
    if ([NSURL fileURLWithPath:(fileStr) isDirectory: NO]) {
        NSURL *file = [NSURL fileURLWithPath:fileStr];
        void *handle;
        load(_util);
        NSLog(@"loading...............bitarry._util");
        PyObject* (*func)(void);
        func = dlsym(_util, "PyInit__util");
        PyImport_AppendInittab("__bitarray__util", func);
    } else {
        NSLog(@"_util dylib is not exist");
        exit(1);
    }
}

void loading_cytoolz_dict() {
    void *dicttoolz = NULL;
    NSString *fileStr = [NSString stringWithFormat:@"%@/Library/Application Support/com.c3-soft.OneKey/app/OneKey/cytoolz/dicttoolz.cpython-38-darwin.so", [[NSBundle mainBundle] resourcePath]];
    if ([NSURL fileURLWithPath:(fileStr) isDirectory: NO]) {
        NSURL *file = [NSURL fileURLWithPath:fileStr];
        void *handle;
        load(dicttoolz);
        PyObject* (*func)(void);
        func = dlsym(dicttoolz, "PyInit_dicttoolz");
        PyImport_AppendInittab("__cytoolz_dicttoolz", func);
    } else {
        NSLog(@"__cytoolz_dicttoolz dylib is not exist");
        exit(1);
    }
}

void loading_cytoolz_func() {
    void *functoolz = NULL;
    NSString *fileStr = [NSString stringWithFormat:@"%@/Library/Application Support/com.c3-soft.OneKey/app/OneKey/cytoolz/functoolz.cpython-38-darwin.so", [[NSBundle mainBundle] resourcePath]];
    if ([NSURL fileURLWithPath:(fileStr) isDirectory: NO]) {
        NSURL *file = [NSURL fileURLWithPath:fileStr];
        void *handle;
        load(functoolz);
        PyObject* (*func)(void);
        func = dlsym(functoolz, "PyInit_dicttoolz");
        PyImport_AppendInittab("__cytoolz_functoolz", func);
    } else {
        NSLog(@"__cytoolz_functoolz dylib is not exist");
        exit(1);
    }
}

void loading_cytoolz_iter() {
    void *itertoolz = NULL;
    NSString *fileStr = [NSString stringWithFormat:@"%@/Library/Application Support/com.c3-soft.OneKey/app/OneKey/cytoolz/itertoolz.cpython-38-darwin.so", [[NSBundle mainBundle] resourcePath]];
    if ([NSURL fileURLWithPath:(fileStr) isDirectory: NO]) {
        NSURL *file = [NSURL fileURLWithPath:fileStr];
        void *handle;
        load(itertoolz);
        PyObject* (*func)(void);
        func = dlsym(itertoolz, "PyInit_itertoolz");
        PyImport_AppendInittab("__cytoolz_itertoolz", func);
    } else {
        NSLog(@"__cytoolz_itertoolz dylib is not exist");
        exit(1);
    }
}

void loading_cytoolz_recipes() {
    void *recipes = NULL;
    NSString *fileStr = [NSString stringWithFormat:@"%@/Library/Application Support/com.c3-soft.OneKey/app/OneKey/cytoolz/recipes.cpython-38-darwin.so", [[NSBundle mainBundle] resourcePath]];
    if ([NSURL fileURLWithPath:(fileStr) isDirectory: NO]) {
        NSURL *file = [NSURL fileURLWithPath:fileStr];
        void *handle;
        load(recipes);
        PyObject* (*func)(void);
        func = dlsym(recipes, "PyInit_recipes");
        PyImport_AppendInittab("__cytoolz_recipes", func);
    } else {
        NSLog(@"__cytoolz_recipes dylib is not exist");
        exit(1);
    }
}

void loading_cytoolz_utils() {
    void *utils = NULL;
    NSString *fileStr = [NSString stringWithFormat:@"%@/Library/Application Support/com.c3-soft.OneKey/app/OneKey/cytoolz/utils.cpython-38-darwin.so", [[NSBundle mainBundle] resourcePath]];
    if ([NSURL fileURLWithPath:(fileStr) isDirectory: NO]) {
        NSURL *file = [NSURL fileURLWithPath:fileStr];
        void *handle;
        load(utils);
        PyObject* (*func)(void);
        func = dlsym(utils, "PyInit_dicttoolz");
        PyImport_AppendInittab("__cytoolz_utils", func);
    } else {
        NSLog(@"__cytoolz_dicttoolz dylib is not exist");
        exit(1);
    }
}

int main(int argc, char *argv[]) {
       NSString * appDelegateClassName;
       int ret = 0;
       NSString *tmp_path;
       NSString *python_home;
       NSString *python_path;
       wchar_t *wpython_home;
       PyThreadState * tstate;
       @autoreleasepool {
        NSString * resourcePath = [[NSBundle mainBundle] resourcePath];
        // Special environment to prefer .pyo; also, don’t write bytecode
        // because the process will not have write permissions on the device.
        putenv("PYTHONIOENCODING=UTF-8"); putenv("PYTHONOPTIMIZE=");
        putenv("PYTHONDONTWRITEBYTECODE=1");
        putenv("PYTHONUNBUFFERED=1");
        // Set the home for the Python interpreter
        python_home = [NSString stringWithFormat:@"%@/Library/Python", resourcePath, nil];
        NSLog(@"PythonHome is: %@", python_home);
        wpython_home = Py_DecodeLocale([python_home UTF8String], NULL);
        Py_SetPythonHome(wpython_home);
        // Set the PYTHONPATH
        python_path = [NSString stringWithFormat:@"PYTHONPATH=%@/Library/Application Support/com.c3-soft.OneKey/app:%@/Library/Application Support/com.c3-soft.OneKey/app_packages:%@/Library/Application Support/com.c3-soft.OneKey/app/OneKey", resourcePath, resourcePath, resourcePath, nil];
        NSLog(@"PYTHONPATH is: %@", python_path);
        putenv((char *)[python_path UTF8String]);
        NSString *documentPath = [NSString stringWithFormat:@"iOS_DATA=%@",[OKStorageManager getDocumentDirectoryPath]];
        putenv((char *)[documentPath UTF8String]);
        // iOS provides a specific directory for temp files.
        tmp_path = [NSString stringWithFormat:@"TMP=%@/tmp", resourcePath, nil];
        putenv((char *)[tmp_path UTF8String]);
        NSLog(@"Initializing Python runtime...");
        loading_cffi_backend();
        loading_lru();
        loading_bitarray_bitarray();
        loading_bitarray_util();
  //      loading_cytoolz_dict();
 //       loading_cytoolz_func();
 //       loading_cytoolz_iter();
 //       loading_cytoolz_recipes();
//        loading_cytoolz_utils();
        Py_Initialize();
        // If other modules are using threads, we need to initialize them.
        PyEval_InitThreads();
        bitarray_importer();
       // cytoolz_importer();
        @try {
          tstate = PyEval_SaveThread();
          appDelegateClassName = NSStringFromClass([AppDelegate class]);
          ret = UIApplicationMain(argc, argv, nil, appDelegateClassName);
          // In a normal iOS application, the following line is what
          // actually runs the application. It requires that the
          // Objective-C runtime environment has a class named
          // "PythonAppDelegate". This project doesn’t define
          // one, because Objective-C bridging isn’t something
          // Python does out of the box. You’ll need to use
          // a library like Rubicon-ObjC [1], Pyobjus [2] or
          // PyObjC [3] if you want to run an *actual* iOS app.
          // [1] http://pybee.org/rubicon
          // [2] http://pyobjus.readthedocs.org/
          // [3] https://pythonhosted.org/pyobjc/
        }
        @catch (NSException *exception) {
         NSLog(@"Python runtime error: %@", [exception reason]);
        }
        @finally {
         PyEval_RestoreThread(tstate);
         Py_Finalize();
        }
        PyMem_RawFree(wpython_home);
        NSLog(@"Leaving...");
       }
       exit(ret);
       return ret;
    }

