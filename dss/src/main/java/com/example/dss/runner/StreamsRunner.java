package com.example.dss.runner;

import org.springframework.lang.Nullable;

public interface StreamsRunner<K, V> {

  void start();

  @Nullable
  default V getValueByKey(K key) {
    return null;
  }
}
