
% On charge la piste de batterie et on calcule le diagramme temps-fr�quence
[y,] = audioread('../Data/essai04/batterie.wav');
y = y(:,1);
N = length(y);
n = 512;
delta = 10000;
nbinstru = 4;
beta = 0.5;
epsilon = 0.01;

xbatterie = abs(diagrammeTF(y, N, n, delta));

% On charge la piste de la m�lodie et on calcule le diagramme temps-fr�quence
[y,] = audioread('../Data/essai04/melodie.wav');
y = y(:,1);

xmelodie = abs(diagrammeTF(y, N, n, delta));

% On charge la piste de la basse et on calcule le diagramme temps-fr�quence
[y,] = audioread('../Data/essai04/basse.wav');
y = y(:,1);

xbasse = abs(diagrammeTF(y, N, n, delta));

% On charge la piste de l'accompagnement et on calcule le diagramme temps-fr�quence
[y,] = audioread('../Data/essai04/accompagnement accord.wav');
y = y(:,1);

xaccompagnement = abs(diagrammeTF(y, N, n, delta));

% On charge la piste de la sortie et on calcule le diagramme temps-fr�quence
[y, Fs] = audioread('../Data/essai04/global.wav');
y = y(:,1);

xglobal = abs(diagrammeTF(y, N, n, delta));

% matrice contenant les TF de chaque instrument dans chaque colonne
% Cette matrice d�pend du temps (on avance colonne par colonne dans les 
% diff�rents diagrammes temps-fr�quance)
W = zeros([n nbinstru]);
compteur = 0;

leng = floor((N-n)/delta);


coefs = zeros([4 leng]);
deter = zeros([1 leng]);


for j=1:leng
    % on met la TF du i-eme instrument � l'instant j dans 
    % la i-eme colonne de W
    W(:,1) = xmelodie(:,j);
    W(:,2) = xaccompagnement(:,j);
    W(:,3) = xbasse(:,j);
    W(:,4) = xbatterie(:,j);
    
    % c sera le nombre d'instruments effectivement 
    % en train de jouer
    c = 4;
    
    % on enregistre dans liste, les indices des
    % instruments qui ne jouent pas 
    % pour que notre probl�me ait une solution
    % (car il faut que les blocs de W soient inversibles)
    liste = [];
    for k=1:nbinstru
        if W(:,k) == zeros([n 1])
            c = c - 1;
        else
            liste = horzcat(liste, [k]);
        end;
    end;
    
    % On construit Wprim � partir de ces 
    % instruments qui jouent (il y en a c)
    Wprim = zeros([n c]);
    
    for k=1:c
        Wprim(:,k) = W(:,liste(k));
    end;
    
    V = xglobal(:,j);
    H = 2*ones([c 1]);
    
    [Xprim, compteur] = multiplication_resolution(V, Wprim, H, beta, epsilon);
    
    % Xprim ne contient que les instruments qui jouent
    % (et pas ceux qu'on a enlev� un peu plus haut)
    % il faut donc reconstruire X en mettant des zeros
    % pour les instruments qui ne jouent pas
    decalage = 0;
    X = zeros([4 1]);
    for k=1:nbinstru
        
        % si k est dans liste on retrouve sa valeur dans Xprim
        if any(k==liste)
            X(k) = Xprim(k-decalage);
        
        % sinon, on met 0
        else
            X(k) = 0;
            decalage = decalage + 1;
        end;
    end;
    
    % on place X dans le diagramme temporel des coefficients
    coefs(:,j) = X;   

end;

% on construit l'�chelle des abscisses
absc = zeros([1 leng]);

for j=1:length(coefs)
    absc(j) = j;
end;

% On affiche les courbes des coeffs
figure
plot(absc,coefs(1,:), absc, coefs(2,:), absc, coefs(3,:), absc, coefs(4,:));