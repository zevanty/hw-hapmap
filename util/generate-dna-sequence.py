import random

outfile = open('sample-sequence.txt', 'w')
SEQLENGTH = 1000000

for i in range(SEQLENGTH):
	text = ''
	rand = random.randint(0, 96)
	if rand < 12:
		text = 'A'
	elif rand < 24:
		text = 'a'
	elif rand < 36:
		text = 'C'
	elif rand < 48:
		text = 'c'
	elif rand < 60:
		text = 'T'
	elif rand < 72:
		text = 't'
	elif rand < 84:
		text = 'G'
	elif rand < 96:
		text = 'g'
	# Convert to a random letter
	else:
		base = 65
		rand2 = random.randint(0, 1)
		if rand2 == 1:
			base = 97
		rand2 = random.randint(0, 25) + base
		text = str(chr(rand2))
	outfile.write(text)

outfile.close()