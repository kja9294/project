import tensorflow as tf
import numpy as np
import math

mcnt = 10
cnt = 0
l_rate = 0.0005
prev = open('./metafile/prev_accuracy_data', 'r+')
temp_data = prev.read()
prev.close()
temp_data = temp_data.split('\n')
prev_accuracy = float(temp_data[0])
init_accuracy = prev_accuracy
while cnt is not mcnt:
    print("previse : ", init_accuracy)
    print("start {} training. learning rate : {}".format(cnt + 1, l_rate))
    train_xy = np.loadtxt('training.csv', delimiter=',' , dtype=np.float32, skiprows=1, usecols=range(1,12))

    x_data = train_xy[:,0:-1]
    y_data = train_xy[:,[-1]]
    xcnt = len(x_data[0])

    x = tf.placeholder(tf.float32, shape=[None, xcnt])
    y = tf.placeholder(tf.float32, shape=[None,1])

    w = tf.Variable(tf.random_normal([xcnt,10]),name='weight')
    b = tf.Variable(tf.random_normal([10]),name = 'bias')
    layer=tf.sigmoid(tf.matmul(x,w)+b)
    
    w2 = tf.Variable(tf.random_normal([10,10]),name='weight2')
    b2 = tf.Variable(tf.random_normal([10]),name = 'bias2')
    layer2=tf.sigmoid(tf.matmul(layer,w2)+b2)
    
    w3 = tf.Variable(tf.random_normal([10,5]),name='weight3')
    b3 = tf.Variable(tf.random_normal([5]),name = 'bias3')
    layer3=tf.sigmoid(tf.matmul(layer2,w3)+b3)
    
    w4 = tf.Variable(tf.random_normal([5,1]),name='weight4')
    b4 = tf.Variable(tf.random_normal([1]),name = 'bias4')
    hypo = tf.sigmoid(tf.matmul(layer3,w4) + b4)
    cost = -tf.reduce_mean(y*tf.log(hypo) + (1-y)*tf.log(1-hypo))
    #train = tf.train.GradientDescentOptimizer(learning_rate = l_rate).minimize(cost)
    train = tf.train.AdamOptimizer(learning_rate = l_rate).minimize(cost)
    prediction= tf.cast(hypo>0.5,dtype=tf.float32)
    accuracy = tf.reduce_mean(tf.cast(tf.equal(prediction, y), dtype=tf.float32))

    with tf.Session() as sess:
       sess.run(tf.global_variables_initializer())
       feed = {x:x_data, y:y_data}
       for step in range(60001):
           sess.run(train, feed_dict=feed)
           if step % 1000 == 0:
               co = sess.run(cost, feed_dict=feed)
               print("\t\t\t\tSTEP: ",step, "\t","LOSS: ",co)
               if math.isnan(co):
                   print("nan is detected, break loop.")
                   break
       h, c, a = sess.run([hypo, prediction, accuracy],feed_dict=feed)
       print("Accuracy: {:.2%}\n".format(a))
       if a > prev_accuracy:
           print(a,">",prev_accuracy ,"Good Accuracy so, change file.")
           saver = tf.train.Saver()
           saver.save(sess, "./metafile/detect_model")
           prev_accuracy = a
           l_rate = l_rate + 0.0001
       if a < 0.8:
       	   l_rate = l_rate - 0.0001
       elif a > 0.9:
       	   l_rate = l_rate + 0.0001
    cnt = cnt + 1

if init_accuracy < prev_accuracy :
    print("Data file will change.")
    prev = open('./metafile/prev_accuracy_data', 'w')
    prev.write("{}\nlearning rate : {}\n".format(prev_accuracy, round(l_rate,2)))
    prev.close()
