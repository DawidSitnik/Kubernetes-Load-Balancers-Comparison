import csv
import os
import pygal
import sys


HOP_LIMIT = 1000
HOP_STEP = 5

LINE_STYLE_ODD = {'width': 3}
LINE_STYLE_EVEN = {'width': 4, 'dasharray': '9, 6', 'linecap': 'round', 'linejoin': 'round'}



def read_tsv(filename: str) -> list:
    counts_per_hop = [0 for _ in range((HOP_LIMIT + 1) // HOP_STEP)]
    # Read the TSV file
    with open(filename) as tsv:
        for row in csv.DictReader(tsv, delimiter="\t"):
            ttime_index = int(row['ttime']) // HOP_STEP
            try:
                counts_per_hop[ttime_index] += 1
            except IndexError:
                pass
    # Adjust to messages per second
    # for i, count in enumerate(counts_per_hop):
    #     counts_per_hop[i] = count * 1000 / HOP_STEP
    return counts_per_hop


if __name__ == '__main__':
    print('The first argument is the output file name, the second argument is its title, '
          'and followings are TSV files.')

    custom_style = pygal.style.Style(
        label_font_size=18,
        major_label_font_size=18,
        title_font_size=24
    )

    our_css_file = os.path.join(os.path.dirname(__file__), 'pygal.css')

    line_chart = pygal.Line(
        css=('file://style.css', 'file://graph.css', 'file://' + our_css_file),
        background='transparent',
        plot_background='transparent',
        colors=('#000000', '#888888'),
        opacity='1.0',
        show_dots=False,
        x_label_rotation=1,
        x_labels_major_every=HOP_LIMIT // (HOP_STEP * 10),
        # legend_at_bottom=True,
        # legend_at_bottom_columns=4,
        # legend_box_size=24,
        # legend_font_size=18,
        show_legend=False,
        # margin=54,
        range=(0, 1500),
        show_minor_x_labels=False,
        style=custom_style
    )
    line_chart.title = sys.argv[2]
    line_chart.x_title = 'Turnaround time (ms)'
    line_chart.y_title = 'Request counts'
    line_chart.x_labels = [ttime for ttime in range(0, HOP_LIMIT, HOP_STEP)]

    for i, tsv_file in enumerate(sys.argv[3:]):
        hop_counts = read_tsv(tsv_file)
        stroke_style: dict = None
        if (i + 1) % 2 == 1:
            stroke_style = LINE_STYLE_ODD
        else:
            stroke_style = LINE_STYLE_EVEN
        line_chart.add('Cluster ' + chr(ord('A') + i), hop_counts, stroke_style=stroke_style)
    
    line_chart.render_to_file(sys.argv[1] + '.svg')
    line_chart.render_to_png(sys.argv[1] + '.png')
    