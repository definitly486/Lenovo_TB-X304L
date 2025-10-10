#!/system/bin/sh

if [ -z "$1" ]
then
     echo "не введен файл для распаковки "
     exit
fi

if [ -z "$2" ]
then
     echo "не введен выходной файл "
     exit
fi

openssl enc -aes-256-cbc -pbkdf2 -iter 100000 -e -d -in $1 $2
