"""Add font-family to SVGs that are missing it."""
import glob
import re

files = [
    'docs/diagram/dfd-level-0.svg',
    'docs/diagram/dfd-level-1-p6.svg',
    'docs/diagram/dfd-top-level.svg',
    'docs/diagram/sequence-login.svg',
    'docs/diagram/sequence-recommend.svg',
    'docs/diagram/use-case.svg',
]

for f in files:
    with open(f, 'r', encoding='utf-8') as fp:
        text = fp.read()

    # Add font-family to SVG tag if not present
    if 'font-family' not in text:
        text = re.sub(
            r'(<svg[^>]*?)(>)',
            r'\1 font-family="Microsoft YaHei, SimHei, sans-serif"\2',
            text
        )

    with open(f, 'w', encoding='utf-8') as fp:
        fp.write(text)
    print(f'Fixed: {f}')
