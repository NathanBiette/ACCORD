function [x, compteur]=methode_nonNul(V, W, H, beta, epsilon)
compteur = 1;
alpha = 0.01;
W_nonNul = W + alpha*ones(size(W));

A = transpose(W_nonNul)*(((W_nonNul*H).^(beta-2)).*V);
B = transpose(W_nonNul)*((W_nonNul*H).^(beta-1));
x = A./B;

J = ones([length(x) 1]);


Vprim = W*H;
V_nonNul = W_nonNul* H ;
%while sum(abs(V-Vprim))>epsilon
while sum(abs(V-Vprim))>epsilon
    H = H.*x;
    A = transpose(W_nonNul)*(((W_nonNul*H).^(beta-2)).*V_nonNul);
    B = transpose(W_nonNul)*((W_nonNul*H).^(beta-1));
    x = A./B;
    
   
    Vprim = W*H;
    V_nonNul = W_nonNul*H
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
    if compteur > 10000
        break;
    end;
end; 
x = H.*x;

% on ne sait pas pourquoi mais ça renvoit toujours compteur = 2 ou 1
return;
end