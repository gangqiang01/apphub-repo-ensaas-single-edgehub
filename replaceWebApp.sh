#!/bin/bash
rm -rf ./src/main/resources/aStore/index.html
rm -rf ./src/main/resources/aStore/static
cp -rfa /home/gangqiangsun/MYPROJECT/FRONTEND/aStore-ensaas-single/dist/*  ./src/main/resources/aStore/
ls -al ./src/main/resources/aStore/
