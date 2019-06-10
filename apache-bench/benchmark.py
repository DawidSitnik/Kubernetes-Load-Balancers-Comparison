import subprocess


POOLS = [16, 32, 64]


if __name__ == '__main__':
    import sys
    import os

    servers_count = 0

    # Change directory
    if __file__ is not None:
        os.chdir(os.path.dirname(os.path.abspath(__file__)))
    else:
        os.chdir(os.path.dirname(os.path.abspath(sys.argv[0])))

    if len(sys.argv) != 1:
        # Message count was provided
        msg_count = int(sys.argv[1])
        with open('servers.txt') as servers_file:
            for i, server_raw in enumerate(servers_file.readlines()):
                server = server_raw.rstrip()
                if len(server) == 0:
                    continue
                for pi, pool in enumerate(POOLS):
                    filename = 'test-{:0>2}-{}.tsv'.format(
                        pool, chr(ord('a') + i))
                    # Benchmark
                    cmdline = './run-ab.sh {} {} {} {}'.format(
                        server, pool, msg_count, filename)
                    print(cmdline)
                    subprocess.call(['bash', '-c', cmdline],
                                    stdout=sys.stdout, stderr=sys.stderr)
                    print('\n')
                servers_count += 1
    else:
        with open('servers.txt') as servers_file:
            for server_raw in servers_file.readlines():
                server = server_raw.rstrip()
                if len(server) == 0:
                    continue
                servers_count += 1

    # Draw graphs
    for pi, pool in enumerate(POOLS):
        files_str = ' '.join(['test-{:0>2}-{}.tsv'
                              ''.format(pool, chr(ord('a') + i))
                              for i in range(servers_count)])
        cmdline = ('python3 ../visuals/hhh.py test-{0:0>2} '
                   '"Turnaround time distribution ({0} pools)" '
                   '{1}'.format(pool, files_str))
        print(cmdline)
        subprocess.call(['bash', '-c', cmdline],
                        stdout=sys.stdout, stderr=sys.stderr)
