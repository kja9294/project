import tensorflow as tf
import numpy as np


#test_xy = np.loadtxt('./testfiles/test3.csv', delimiter=',' , dtype=np.float32, skiprows=1 , usecols=range(1,11))
test_xy = np.loadtxt('./log/pcm.csv', delimiter=';' , dtype=np.float32, skiprows=2 , usecols=range(2,12))


xcnt = len(test_xy[0])

x = tf.placeholder(tf.float32, shape=[None, xcnt])
y = tf.placeholder(tf.float32, shape=[None,1])

with tf.Session() as sess:
    saver = tf.train.import_meta_graph('./metafile/detect_model.meta')
    saver.restore(sess,tf.train.latest_checkpoint('./metafile/'))

    graph = tf.get_default_graph()
    w = sess.run(graph.get_tensor_by_name('weight:0'))
    b = sess.run(graph.get_tensor_by_name('bias:0'))
    hypo = tf.sigmoid(tf.matmul(x,w) + b)
    prediction= tf.cast(hypo>0.5,dtype=tf.float32)
    res = sess.run(prediction, feed_dict = {x: test_xy})
    atk = 0
    for i in res:
   	    if i[0] == 1:
   		    atk = atk+1
    if atk:
   	    print('Attack Detected. {} seconds under attacked.'.format(atk))
    else:
        print('Attack Not Detected.')    


