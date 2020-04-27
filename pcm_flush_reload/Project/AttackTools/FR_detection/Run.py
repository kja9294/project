# FLUSH+RELOAD detection code.
# Created by JongHyeon Cho in kwangwoon uni. 2018
# Copyright @ 2018 JongHyeon Cho All right reserved.

import detection as dtt
import subprocess
import os
import numpy as np
import time 
import sys

cur_line = 0 #for skip line.
pid = os.fork()

if pid == 0: #must execute this code root authority.
    subprocess.call(['./pcm.x','-nc','-ns','0.5','-csv=./log/pcm.csv'])
    os._exit(0)
else: 
    time.sleep(1.6)
    pr_flag = 1 #print flag. for printing detection message only once.
    count=0
    print("\nDetecting Unit program started.\n")
    while 1:
        input = np.loadtxt('./log/pcm.csv', delimiter=';' , dtype=np.float32, skiprows=2+cur_line, usecols=range(2,12))
        if sys.getsizeof(input[0]) > 30:
            input = input[len(input)-1]

        #print(input, cur_line) #for testing.
        cur_line = cur_line + 1
        time.sleep(0.5)

        input_func = []
        input_func.append(input)

        result = dtt.detection(input_func) #run detection code.
        print(result, cur_line)
        if result and pr_flag:
            if pr_flag:
                print("\n\t  -----------------  ")
                print("\t!                   !")
                print("\t!  Attack Detected! !")
                print("\t!                   !")
                print("\t  ------------- ")
                pr_flag = 0
        elif not result and not pr_flag:
            pr_flag = 1

        if cur_line is 20:
            f = open('./log/pcm.csv','r+')
            f.truncate()
            f.close()
            time.sleep(0.8)
            cur_line = -2
