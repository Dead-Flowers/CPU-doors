import time


def get_now_ms():
    return time.time_ns() // 1_000_000
