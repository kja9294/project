# FLUSH+RELOAD detection code.
# Created by JongHyeon Cho in kwangwoon uni. 2018
# Copyright @ 2018 JongHyeon Cho All right reserved.

import tensorflow as tf
import numpy as np

def detection(input_xy):
    xcnt = len(input_xy[0])
    x = tf.placeholder(tf.float32, shape=[None, xcnt])
    y = tf.placeholder(tf.float32, shape=[None,1])

    sess = tf.Session()
    saver = tf.train.import_meta_graph('./metafile/detect_model.meta')
    saver.restore(sess,tf.train.latest_checkpoint('./metafile/'))

    graph = tf.get_default_graph()
    w = sess.run(graph.get_tensor_by_name('weight:0'))
    b = sess.run(graph.get_tensor_by_name('bias:0'))
    hypo = tf.sigmoid(tf.matmul(x,w) + b)
    prediction= tf.cast(hypo>0.5,dtype=tf.float32)
    res = sess.run(prediction, feed_dict = {x: input_xy})
    sess.close()
    tf.global_variables_initializer()
    return res[0][0]


