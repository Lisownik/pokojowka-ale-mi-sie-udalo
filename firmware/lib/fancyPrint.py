LOG_CHAR_CNT = 60

def print_with_style(text, char_cnt=LOG_CHAR_CNT):
    import math
    text = text.strip()
    char = '-'
    chars = math.floor((char_cnt - len(text) - 2)/2)
    print(char * chars + " " + text + " " + char * chars)