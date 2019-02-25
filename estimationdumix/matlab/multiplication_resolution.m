function [x, compteur, conv]=multiplication_resolution(V, W, H, beta, epsilon)
compteur = 1;

%PB : quand il y a des cases nulles dans la WH ou V on a ensuite NaN
%solution possible, ajouter à V et à chaque colonne de W*H un epsilon
%en fonction de la valeur précédente

lim = 10000;

conv = zeros([length(H) 10000]);

Hprim = H + 2*epsilon*ones(size(H));

Vprim = W*H;
conv(:,1) = H;

Vprim = W*H;
while sum(abs(H-Hprim))>epsilon
    Hprim = H;
    A = transpose(W)*(((W*H).^(beta-2)).*V);
    B = transpose(W)*((W*H).^(beta-1));
    x = A./B;
    
    H = H.*x;
    
    Vprim = W*H;
    
    % Utiliser min(x) s'est trouvé empiriquement préférable à max
    % if max(x) > 1
    %    x = ones(size(x))./x;
    % end;
    
    %for i=1:length(x)
    %    if x(i) > 1
    %        x(i) = 1/x(i);
    %    end;
    %end;
    
    
    % cette boucle n'est jamais appelée lorsqu'on rajoute 
    % la boucle if ci-dessus
    compteur = compteur + 1;
    conv(:,compteur) = H;
    if compteur >= lim
        break;
    end;
end; 
x = H;

return;
end