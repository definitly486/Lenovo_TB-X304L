#!/system/bin/sh

if [ -z "$1" ]
then
     echo "не введен пароль для gh"
     exit
fi



if ! [  -f "gh.tar.xz.enc" ]; then
     curl -L -o   gh.tar.xz.enc  https://github.com/definitly486/definitly486/releases/download/gh/gh.tar.xz.enc
fi


openssl enc -aes-256-cbc -pbkdf2 -iter 100000 -e  -d  -in gh.tar.xz.enc  -out gh.tar.xz -pass pass:$1
tar xf  gh.tar.xz
mkdir -p $HOME/.config
cp -r gh $HOME/.config