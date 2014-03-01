#!/usr/bin/perl

my $url = 'http://www.ec.gc.ca/indicateurs-indicators/25C196D8-5835-4A86-BCD0-7731C7109610/AirQuality_O3_Nat_EN.csv';
my $counter = 0;

my @data = `curl --silent "$url"`;

print "ID|TIME|VALUE|MAX\n";

for(my $count = 0; $count < @data; $count ++) {
    my $line = $data[$count];
    $line =~ s/\s+$//;

    next if($count < 3);  # headers

    my ($year, $average, $peak) = split /,/, $line;

    last if($year eq '');  # end of data
    print "$counter|$year|$average|$peak\n";
    $counter ++;
}
