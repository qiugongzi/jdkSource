
  public synchronized static void free(FastStringBuffer sb)
  {



    sb.setLength(0);
    m_stringBufPool.freeInstance(sb);
  }
}
