from time import perf_counter

def get_time(func):
    def wrapper(*args, **kwargs):
        start_time = perf_counter()
        func(*args, *kwargs)
        end_time = perf_counter()
        total_time = round(end_time - start_time, 2)
        print(f"Function '{func.__name__}' took {total_time} seconds.")
    return wrapper