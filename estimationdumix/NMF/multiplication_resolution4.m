function [x, compteur]=multiplication_resolution4(V, W, H, beta, epsilon)
compteur = 0;
x = ((transpose(W)*(((W*H).^(beta-2)).*V))./(transpose(W)*((W*H).^(beta-1))));

Hprim = H + 2*epsilon*ones(size(H));


while abs(sum(Hprim)-sum(H))>epsilon
    Hprim = H;
    H = H.*x;
    x = ((transpose(W)*(((W*H).^(beta-2)).*V))./(transpose(W)*((W*H).^(beta-1))));
    
    compteur = compteur + 1;
    if compteur > 1000
        break;
    end;
end; 
x = H;
return;
end