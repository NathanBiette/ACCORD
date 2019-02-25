function x=fft_morceau(y)
% fonction qui calcule la fft d'une liste y apr�s 
% l'avoir modul�e par la fonction de Hann
len = length(y);
x = y.*hamming(len);

x = fft(x);
return;
end