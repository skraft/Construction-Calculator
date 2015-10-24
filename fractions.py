import math

input = .25
resolution = 16.0

numString = str(input).split('.')[1]
num = int(numString)
den = math.pow(10, len(numString))  # raise 10 to the power of the length  (25 becomes 100)

# convert fraction to resolution
reducer = den / resolution
num = int(round(num / reducer))
den = int(resolution)

# reduce
while num > 1 and den > 1:
	if not num % 2 and not den % 2: # numerator and denominator can be split in half evenly
		num = num / 2
		den = den / 2
	else:
		break

print num, '/', den