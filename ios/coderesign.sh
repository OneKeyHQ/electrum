#!/bin/bash
set -e -u
read -p "请输入证书名称："  key

if [ -z "${key}" ]; then
   echo "证书名称不能为空"
   exit 1
fi
set -x
find Support -name '*.so' -exec  codesign -fs  "${key}" {} \;
set +x
if [ "$?" != 0 ]; then
    echo "签名异常，请重试！！！！！"
else
  echo "签名成功！！！"
fi