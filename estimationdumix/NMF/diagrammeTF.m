function x=diagrammeTF(y, N, n, delta)
% fonction qui fait le diagramme TF d'un signal enregistr� dans y
% de longueur N que l'on d�coupe en morceaux de longueur n d�cal�s 
% de delta � chaque it�ration
k = 1;
compteur = 1;

% pr�-allocation de la matrice pour optimiser le calcul
x = zeros([n floor((N-n)/delta)]);

x(:,1) = fft_morceau(y(k:k+n-1));
k = k + delta;

while (k + n-1)<N
    % on r�alise la FFT de y entre k et k+n-1
    x(:,compteur) = fft_morceau(y(k:k+n-1));
    k = k + delta;
    compteur = compteur + 1;
end;
return;
end
