function x=fft_morceau(y)
% fonction qui calcule la fft d'une liste y apr�s 
% l'avoir modul�e par la fonction de Hann
len = length(y);
x = zeros([1 len]);

% on multiplie y par une fen�tre de Hann
for i=1:len
    x(1,i) = y(i)*(( 1/2) - (1/2)*cos(2*pi*((i-1)/(len-1))));
end;

%ou bien on peut utiliser directement la fonction hann de Matlab nn ?
x = fft(x);
return;
end