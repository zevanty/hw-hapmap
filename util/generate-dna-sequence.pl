my $rand;
my $SEQLENGTH= 1000000;

open OUTFILE, "> sample-sequence.txt";

for (my $i = 0; $i < $SEQLENGTH; $i++) {
	my $text = "";
	$rand = int(rand(96)) + 1;
	if ($rand < 12) {
		$text = 'A';
	}
	elsif ($rand < 24) {
		$text = 'a';
	}
	elsif ($rand < 36) {
		$text = 'C';
	}
	elsif ($rand < 48) {
		$text = 'c';
	}
	elsif ($rand < 60) {
		$text = 'T';
	}
	elsif ($rand < 72) {
		$text = 't';
	}
	elsif ($rand < 84) {
		$text = 'G';
	}
	elsif ($rand < 96) {
		$text = 'g';
	}
	# Convert to a random letter
	else {
		my $base = 65;
		my $rand2 = int(rand(2));
		if ($rand2 == 1) {
			$base = 97;
		}
		$rand2 = int(rand(26)) + $base;
		$text = chr($rand2);
	}
	print OUTFILE "$text";
}

close OUTFILE;