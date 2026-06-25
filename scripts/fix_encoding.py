#!/usr/bin/env python3
"""Fix garbled UTF-8 characters in the detailed design document."""
import os

os.chdir(os.path.dirname(os.path.abspath(__file__)))

with open('docs/详细设计/详细设计说明书.md', 'rb') as f:
    raw = f.read()

# The file has U+FFFD characters embedded. Let's figure out what they should be.
# First decode, find all U+FFFD positions
text = raw.decode('utf-8', errors='replace')

# Find all U+FFFD positions with context
positions = []
for i, ch in enumerate(text):
    if ch == '\ufffd':
        # Get context
        start = max(0, i - 10)
        end = min(len(text), i + 10)
        context = text[start:end]
        # Clean the context for displaying
        clean_ctx = ''.join(c if c != '\ufffd' else '?' for c in context)
        positions.append({'pos': i, 'context': clean_ctx, 'start': start, 'end': end})

print(f'Total U+FFFD found: {len(positions)}')
for p in positions[:50]:
    print(f"  pos={p['pos']} ctx=[{p['context']}]")