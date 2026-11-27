def get_arg_id(argv):
    for a in argv[1:]:
        if a.startswith("--id="):
            return a[5:]
    return None



def read_id_file(filepath):
    try:
        with open(filepath, 'r') as file:
            text = file.read()
            if text == "":
                raise ValueError("Id not stored in id.txt")
            return text
    except OSError or ValueError:
        reg_id = "tried-to-register-id-no-implementation"
        if not reg_id:
            raise Exception("No server no id file or id in file")
        with open(filepath, 'w') as file:
            file.write(reg_id)
        return reg_id


def get_my_id(sys, filepath):
    arg_id = get_arg_id(sys.argv)
    if arg_id:
        return arg_id
    return read_id_file(filepath)